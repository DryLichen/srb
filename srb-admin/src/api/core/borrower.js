import request from '@/utils/request'

export default {
  // 获取分页的借款人列表
  getListPage(page, limit, keyword) {
    return request({
      url: `/admin/core/borrower/list/${page}/${limit}`,
      method: 'get',
      params: {
        keyword: keyword,
      },
    })
  },

  // 展示借款人详细信息
  show(id) {
    return request({
      url: `/admin/core/borrower/show/${id}`,
      method: 'get',
    })
  },

  approval(approvalForm) {
    return request({
      url: '/admin/core/borrower/approval',
      method: 'post',
      data: approvalForm,
    })
  },
}
