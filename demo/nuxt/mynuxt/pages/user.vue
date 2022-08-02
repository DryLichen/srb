<!-- 测试自动路由 -->
<template>
  <div>
    <h1>用户中心</h1>
    <nuxt-link to="/user">
      用户信息
    </nuxt-link>
    <nuxt-link to="/user/account">
      用户账户
    </nuxt-link>

    <!-- 测试嵌套路由 -->
    <!-- 路由出口 -->
    <NuxtChild/>
  </div>
</template>

<script>
export default {
  name: "user",
  layout: "my"
}
</script>

<style scoped>

</style>
