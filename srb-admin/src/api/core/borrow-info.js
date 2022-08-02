import request from '@/utils/request'

export default {
  //获取分页的借款申请列表
  getListPage(page, limit, keyword) {
    return request({
      url: `/admin/core/borrowInfo/list/${page}/${limit}`,
      method: 'get',
      params: keyword,
    })
  },

  // 获取借款申请详情
  show(id) {
    return request({
      url: `/admin/core/borrowInfo/show/${id}`,
      method: 'get',
    })
  },

  //审批借款申请
  approval(borrowInfoApproval) {
    return request({
      url: '/admin/core/borrowInfo/approval',
      method: 'post',
      data: borrowInfoApproval,
    })
  },
}
