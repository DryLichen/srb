// 定义查询用户等级列表的axios
// @ is the alias for src
import request from '@/utils/request'

export default {
  list() {
    return request({
      url: '/admin/core/integralGrade/list',
      method: 'get',
    })
  },
  removeById(id) {
    return request({
      // es6模板字符串
      url: `/admin/core/integralGrade/remove/${id}`,
      method: 'delete',
    })
  },
  save(integralGrade) {
    return request({
      url: '/admin/core/integralGrade/save',
      method: 'post',
      data: integralGrade, //data关键字，封装json数据
    })
  },
  getById(id) {
    return request({
      url: `admin/core/integralGrade/get/${id}`,
      method: 'get',
    })
  },
  update(integralGrade) {
    return request({
      url: '/admin/core/integralGrade/update',
      method: 'put',
      data: integralGrade,
    })
  },
}
