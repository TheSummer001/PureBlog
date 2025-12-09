import { defineConfig } from 'unocss'
import presetIcons from '@unocss/preset-icons'

export default defineConfig({
    presets: [
        presetIcons({
            scale: 1.2,
            extraProperties: {
                'display': 'inline-block',
                'vertical-align': 'middle',
            },
        }),
    ],
    theme: {
        colors: {
            primary: {
                50: '#f5f3ff',
                100: '#ede9fe',
                200: '#ddd6fe',
                300: '#c4b5fd',
                400: '#a78bfa',
                500: '#8b5cf6',
                600: '#7c3aed',
                700: '#6d28d9',
                800: '#5b21b6',
                900: '#4c1d95',
                DEFAULT: '#4f46e5' // 主色调紫色
            },
            success: '#52c41a',
            warning: '#faad14',
            error: '#ff4d4f',
        },
    },
})