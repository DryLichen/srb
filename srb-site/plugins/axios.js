import { Message } from 'element-ui'
import cookie from 'js-cookie'

export default function({ $axios, redirect }) {
  // 在发起请求中添加token
  $axios.onRequest((config) => {
    let userInfo = cookie.get('userInfo')
    if (userInfo) {
      console.log('统一在拦截器中向header添加token')
      userInfo = JSON.parse(userInfo)
      config.headers['token'] = userInfo.token
    }
    console.log('Making request to ' + config.url)
  })

  // 处理发起请求错误
  $axios.onRequestError((error) => {
    console.log('onRequestError', error) // for debug
  })

  // 处理后端响应未登录
  $axios.onResponse(response => {
    console.log('Receiving response', response)
    if (response.data.code === 0) {
      return response
    } else if (response.data.code === -211) {
      console.log('用户校验失败')
      cookie.set('userInfo', '')
      // 未登录则跳转回登录界面
      window.location.href = '/login'
    } else {
      Message({
        message: response.data.message,
        type: 'error',
        duration: 5 * 1000,
      })
      return Promise.reject(response)
    }
  })

  // 通信失败
  $axios.onResponseError((error) => {
    console.log('onResponseError', error) // for debug
  })
}
