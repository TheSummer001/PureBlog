<template>
  <div class="home-container">
    <div class="content-wrapper">
      <div class="article-list">
        <div v-if="loading" class="skeleton-list">
          <n-card v-for="i in 3" :key="i" class="article-item mb-4">
            <n-skeleton text :repeat="2" />
            <n-skeleton text style="width: 60%; margin-top: 10px;" />
          </n-card>
        </div>

        <template v-else>
          <div v-if="articleList.length > 0">
            <n-card 
              v-for="item in articleList" 
              :key="item.id" 
              class="article-item hover:shadow-lg transition-shadow duration-300 mb-4 cursor-pointer"
              @click="router.push(`/article/${item.id}`)"
            >
              <div class="flex flex-col gap-2">
                <h2 class="text-xl font-bold text-gray-800 dark:text-gray-100 hover:text-primary transition-colors">
                  {{ item.title }}
                  <n-tag v-if="item.isTop" type="warning" size="small" class="ml-2">置顶</n-tag>
                </h2>
                
                <div class="text-gray-500 text-sm flex items-center gap-4">
                  <span>📅 {{ item.publishTime }}</span>
                  <span>👁️ {{ item.views }} 阅读</span>
                  <span>📂 {{ item.categoryName }}</span>
                </div>

                <p class="text-gray-600 dark:text-gray-400 mt-2 line-clamp-3">
                  {{ item.summary || '暂无摘要' }}
                </p>

                <div class="tags mt-2">
                  <n-tag 
                    v-for="tag in item.tags" 
                    :key="tag.id" 
                    size="small" 
                    :color="{ borderColor: 'transparent', color: '#f0f0f0', textColor: '#666' }"
                    class="mr-2"
                  >
                    # {{ tag.name }}
                  </n-tag>
                </div>
              </div>
            </n-card>

            <div class="flex justify-center mt-8">
              <n-pagination
                v-model:page="query.pageNum"
                :page-count="totalPage"
                @update:page="handlePageChange"
              />
            </div>
          </div>

          <n-empty v-else description="博主很懒，暂时还没有发布文章~" class="mt-20" />
        </template>
      </div>

      <div class="sidebar hidden md:block">
        <n-card title="关于博主" class="mb-4">
          <div class="flex flex-col items-center">
            <n-avatar round :size="80" src="https://07akioni.oss-cn-beijing.aliyuncs.com/07akioni.jpeg" />
            <h3 class="mt-2 font-bold">TheSummer</h3>
            <p class="text-gray-500 text-sm mt-1">热爱技术，分享生活</p>
            <div class="flex gap-4 mt-4">
              <n-button circle size="small">
                <template #icon>G</template>
              </n-button>
              <n-button circle size="small">
                <template #icon>W</template>
              </n-button>
            </div>
          </div>
        </n-card>

        <n-card title="热门文章">
          <n-list hoverable clickable>
            <n-list-item v-for="hot in hotArticles" :key="hot.id" @click="router.push(`/article/${hot.id}`)">
              <div class="flex justify-between items-center">
                <span class="truncate flex-1">{{ hot.title }}</span>
                <span class="text-xs text-gray-400">🔥{{ hot.views }}</span>
              </div>
            </n-list-item>
          </n-list>
        </n-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { getPublicArticleList, getHotArticles } from '@/api/article'
import type { ArticleListVO, ArticleQueryDTO } from '@/types/article'
import { NCard, NSkeleton, NTag, NPagination, NEmpty, NAvatar, NButton, NList, NListItem, useMessage } from 'naive-ui'

const router = useRouter()
const message = useMessage()

// 数据状态
const loading = ref(true)
const articleList = ref<ArticleListVO[]>([])
const hotArticles = ref<ArticleListVO[]>([])
const totalPage = ref(1)

// 查询参数
const query = reactive<ArticleQueryDTO>({
  pageNum: 1,
  pageSize: 10
})

// 获取文章列表
const fetchArticles = async () => {
  loading.value = true
  try {
    const res = await getPublicArticleList(query)
    articleList.value = res.records
    totalPage.value = res.pages
  } catch (error) {
    message.error('获取文章列表失败')
  } finally {
    loading.value = false
  }
}

// 获取热门文章
const fetchHotArticles = async () => {
  try {
    const res = await getHotArticles()
    hotArticles.value = res.records
  } catch (error) {
    console.error('获取热门文章失败', error)
  }
}

// 分页切换
const handlePageChange = (page: number) => {
  query.pageNum = page
  fetchArticles()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 初始化
onMounted(() => {
  fetchArticles()
  fetchHotArticles()
})
</script>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.content-wrapper {
  display: flex;
  gap: 24px;
}

.article-list {
  flex: 1;
  min-width: 0; /* 防止 flex 子项溢出 */
}

.sidebar {
  width: 300px;
  flex-shrink: 0;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .content-wrapper {
    flex-direction: column;
  }
  .sidebar {
    width: 100%;
  }
}
</style>