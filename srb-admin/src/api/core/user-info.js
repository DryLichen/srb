import request from '@/utils/request'

export default {
  getListPage(page, limit, searchObj) {
    return request({
      url: `/admin/core/userInfo/list/${page}/${limit}`,
      method: 'get',
      params: searchObj, //这里没有用{:}是因为后端有查询bean
    })
  },

  lock(id, status) {
    return request({
      url: `/admin/core/userInfo/lock/${id}/${status}`,
      method: 'put',
    })
  },

  getUserLoginRecords50(userId) {
    return request({
      url: `/admin/core/userLoginRecord/listTop50/${userId}`,
      method: 'get',
    })
  },
}
