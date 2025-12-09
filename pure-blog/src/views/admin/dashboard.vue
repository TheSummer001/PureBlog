<template>
  <div class="dashboard">
    <h1>仪表盘</h1>
    <n-grid cols="1 s:2 m:4" responsive="screen" :x-gap="12" :y-gap="12">
      <n-grid-item>
        <n-card title="文章总数">
          <div class="statistic">
            <n-statistic tabular-nums>
              <n-number-animation ref="numberAnimationInstRef" :from="0" :to="123" />
            </n-statistic>
          </div>
        </n-card>
      </n-grid-item>
      <n-grid-item>
        <n-card title="分类数">
          <div class="statistic">
            <n-statistic tabular-nums>
              <n-number-animation ref="numberAnimationInstRef" :from="0" :to="12" />
            </n-statistic>
          </div>
        </n-card>
      </n-grid-item>
      <n-grid-item>
        <n-card title="标签数">
          <div class="statistic">
            <n-statistic tabular-nums>
              <n-number-animation ref="numberAnimationInstRef" :from="0" :to="24" />
            </n-statistic>
          </div>
        </n-card>
      </n-grid-item>
      <n-grid-item>
        <n-card title="总访问量">
          <div class="statistic">
            <n-statistic tabular-nums>
              <n-number-animation ref="numberAnimationInstRef" :from="0" :to="12345" />
            </n-statistic>
          </div>
        </n-card>
      </n-grid-item>
    </n-grid>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.statistic {
  text-align: center;
  padding: 20px 0;
}
</style>