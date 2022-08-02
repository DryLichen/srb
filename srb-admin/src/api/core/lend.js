import request from '@/utils/request'

export default {
  // 展示标的列表
  getList(keyword) {
    return request({
      url: '/admin/core/lend/list',
      method: 'get',
      params: {
        keyword: keyword,
      },
    })
  },
  // 展示标的详情
  show(id) {
    return request({
      url: `/admin/core/lend/show/${id}`,
      method: 'get',
    })
  },

  // 放款
  makeLoan(id) {
    return request({
      url: `/admin/core/lend/makeLoan/${id}`,
      method: 'get',
    })
  },
}
