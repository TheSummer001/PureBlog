import { ref } from 'vue'
import request from '@/utils/request'

// 图片上传 Hook
export const useImageUpload = () => {
    // 上传状态
    const uploading = ref(false)
    const uploadError = ref<string | null>(null)

    // 上传图片函数
    const uploadImage = async (files: File[]): Promise<string[]> => {
        uploading.value = true
        uploadError.value = null

        try {
            // 创建 FormData 对象
            const formData = new FormData()

            // 添加文件到 FormData
            files.forEach((file, index) => {
                formData.append(`file${index}`, file)
            })

            // 调用后端上传接口
            const response: any = await request.post('/infra/file/upload', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            })

            // 返回上传后的图片 URL 列表
            // 假设后端返回的是图片 URL 数组
            return Array.isArray(response) ? response : [response]
        } catch (error: any) {
            uploadError.value = error.message || '图片上传失败'
            console.error('Image upload failed:', error)
            return []
        } finally {
            uploading.value = false
        }
    }

    // 处理 Markdown 编辑器的图片上传事件
    const handleEditorImageUpload = async (files: File[], callback: (urls: string[]) => void) => {
        try {
            const urls = await uploadImage(files)
            callback(urls)
        } catch (error) {
            console.error('Editor image upload failed:', error)
            callback([])
        }
    }

    return {
        uploading,
        uploadError,
        uploadImage,
        handleEditorImageUpload
    }
}