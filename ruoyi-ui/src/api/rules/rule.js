import request from '@/utils/request'

export function listRule(query) {
  return request({
    url: '/rules/model/list',
    method: 'get',
    params: query
  })
}

export function changeModelStatus(id, active) {
  const data = {
    id: id,
    active: active
  }
  return request({
    url: '/rules/model/changeStatus',
    method: 'put',
    data: data
  })
}

export function updateRule(data) {
  return request({
    url: '/rules/model/update',
    method: 'put',
    data: data
  })
}

export function addRule(data) {
  return request({
    url: 'rules/model/add',
    method: 'put',
    data: data
  })
}
