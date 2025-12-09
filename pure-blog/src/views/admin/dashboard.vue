<template>
  <div class="dashboard-container">
    <n-grid cols="1 s:2 m:4" responsive="screen" :x-gap="16" :y-gap="16">
      <n-grid-item>
        <n-card class="stat-card" :bordered="false">
          <div class="stat-content">
            <div class="stat-icon bg-blue-100 text-blue-600">
              <n-icon size="24">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="currentColor" d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6zM6 20V4h7v5h5v11H6z"/></svg>
              </n-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">文章总数</div>
              <n-statistic>
                <n-number-animation :from="0" :to="stats.articleCount" />
              </n-statistic>
            </div>
          </div>
        </n-card>
      </n-grid-item>

      <n-grid-item>
        <n-card class="stat-card" :bordered="false">
          <div class="stat-content">
            <div class="stat-icon bg-green-100 text-green-600">
              <n-icon size="24">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="currentColor" d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5s5 2.24 5 5s-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3s3-1.34 3-3s-1.34-3-3-3z"/></svg>
              </n-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">总阅读量</div>
              <n-statistic>
                <n-number-animation :from="0" :to="stats.viewCount" show-separator />
              </n-statistic>
            </div>
          </div>
        </n-card>
      </n-grid-item>

      <n-grid-item>
        <n-card class="stat-card" :bordered="false">
          <div class="stat-content">
            <div class="stat-icon bg-orange-100 text-orange-600">
              <n-icon size="24">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="currentColor" d="M10 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2h-8l-2-2z"/></svg>
              </n-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">分类数</div>
              <n-statistic>
                <n-number-animation :from="0" :to="stats.categoryCount" />
              </n-statistic>
            </div>
          </div>
        </n-card>
      </n-grid-item>

      <n-grid-item>
        <n-card class="stat-card" :bordered="false">
          <div class="stat-content">
            <div class="stat-icon bg-purple-100 text-purple-600">
              <n-icon size="24">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="currentColor" d="M21.41 11.58l-9-9C12.05 2.22 11.55 2 11 2H4c-1.1 0-2 .9-2 2v7c0 .55.22 1.05.59 1.42l9 9c.36.36.86.58 1.41.58c.55 0 1.05-.22 1.41-.59l7-7c.37-.36.59-.86.59-1.41c0-.55-.23-1.06-.59-1.42M5.5 7C4.67 7 4 6.33 4 5.5S4.67 4 5.5 4 7 4.67 7 5.5 6.33 7 5.5 7"/></svg>
              </n-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">标签数</div>
              <n-statistic>
                <n-number-animation :from="0" :to="stats.tagCount" />
              </n-statistic>
            </div>
          </div>
        </n-card>
      </n-grid-item>
    </n-grid>

    <n-grid cols="1" responsive="screen" :x-gap="16" :y-gap="16" class="mt-4">
      <n-grid-item>
        <n-card title="文章发布趋势 (近30天)" :bordered="false" class="chart-card">
          <div ref="chartRef" class="chart-container"></div>
        </n-card>
      </n-grid-item>
    </n-grid>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, reactive } from 'vue'
import { 
  NGrid, NGridItem, NCard, NStatistic, NNumberAnimation, NIcon, useMessage 
} from 'naive-ui'
import * as echarts from 'echarts'
import { getDashboardStats, getPublishRecord } from '@/api/dashboard'
import type { DashboardStats } from '@/api/dashboard'

const message = useMessage()

// 统计数据
const stats = reactive<DashboardStats>({
  articleCount: 0,
  viewCount: 0,
  categoryCount: 0,
  tagCount: 0
})

// 图表 DOM 引用
const chartRef = ref<HTMLElement | null>(null)
let chartInstance: echarts.ECharts | null = null

// 初始化数据
const initData = async () => {
  try {
    // 1. 获取基础统计
    const statsRes = await getDashboardStats()
    Object.assign(stats, statsRes) // 自动解包后的数据直接赋值

    // 2. 获取图表数据 (最近30天)
    const records = await getPublishRecord(30)
    initChart(records)
  } catch (error) {
    message.error('获取仪表盘数据失败')
    console.error(error)
  }
}

// 初始化图表
const initChart = (data: { date: string; count: number }[]) => {
  if (!chartRef.value) return

  // 销毁旧实例
  if (chartInstance) {
    chartInstance.dispose()
  }

  chartInstance = echarts.init(chartRef.value)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: data.map(item => item.date)
    },
    yAxis: {
      type: 'value',
      minInterval: 1
    },
    series: [
      {
        name: '发布文章数',
        type: 'line',
        stack: 'Total',
        smooth: true,
        lineStyle: {
          width: 3,
          color: '#1890ff'
        },
        areaStyle: {
          opacity: 0.1,
          color: '#1890ff'
        },
        itemStyle: {
          color: '#1890ff'
        },
        data: data.map(item => item.count)
      }
    ]
  }

  chartInstance.setOption(option)
}

// 窗口大小变化时重绘图表
const handleResize = () => {
  chartInstance?.resize()
}

onMounted(() => {
  initData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>

<style scoped>
.dashboard-container {
  padding: 16px;
}

.stat-card {
  transition: transform 0.3s ease;
  cursor: default;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 4px;
}

.n-statistic {
  font-weight: bold;
}

.chart-card {
  min-height: 400px;
}

.chart-container {
  width: 100%;
  height: 350px;
}

/* 简单的 Tailwind-like 工具类，如果 UnoCSS 生效可以省略，这里为了保险起见 */
.bg-blue-100 { background-color: #e6f7ff; }
.text-blue-600 { color: #1890ff; }
.bg-green-100 { background-color: #f6ffed; }
.text-green-600 { color: #52c41a; }
.bg-orange-100 { background-color: #fff7e6; }
.text-orange-600 { color: #faad14; }
.bg-purple-100 { background-color: #f9f0ff; }
.text-purple-600 { color: #722ed1; }
.mt-4 { margin-top: 16px; }
</style>