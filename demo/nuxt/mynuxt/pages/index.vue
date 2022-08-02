<template>
  <div>
    <nuxt-link to="/about">
      关于我们
    </nuxt-link>
    <nuxt-link to="/lend">
      我要投资
    </nuxt-link>
    <nuxt-link to="/user">
      用户中心
    </nuxt-link>
    <a href="www.baidu.com" target="_blank"></a>

    <h1>Home Page</h1><br/>

    <!-- 测试远程数据绑定，获取ip地址 -->
    <div>IP地址是：{{ ip1 }}</div>
  </div>
</template>

<script>
export default {
  data(){
    return {
      ip: null
    }
  },

  // 普通的客户端渲染，使用了nuxt的内置axios
  created() {
    // $get可以直接获取数据data，去掉普通get的其他信息
    // this.$axios.$get('http://icanhazip.com').then(response => {
    //   console.log(response)
    //   this.ip = response
    // })
  },

  // 结构赋值，只要page里的axios, page.axios
  // 无法获取到值，因为异步操作，response还未得到就进行了赋值
//   asyncData({$axios}){
//     console.log('asyncData')
//     $axios.$get('http://icanhazip.com').then(response => {
//       console.log(response)
//     })
//     return {
//       ip: response
//     }
//   }

  // 服务器端渲染,变量名不能和data里的重复
  async asyncData({ $axios }){
    console.log('asyncData')
    let response = await $axios.$get('/')
    return {
      ip1: response
    }
  }
}
</script>
