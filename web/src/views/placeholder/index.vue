<script setup>
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<template>
  <el-card shadow="never">
    <el-empty :description="`${route.meta.title || '页面'}待实现（M1 占位）`" />
  </el-card>
</template>
