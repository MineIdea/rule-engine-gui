import request from '@/utils/request'

export function listSource(query) {
  return request({
    url: '/rules/source/list',
    method: 'get',
    params: query
  })
}

export function changeSourceStatus(id, active) {
  const data = {
    id: id,
    status: active
  }
  return request({
    url: '/rules/source/changeStatus',
    method: 'put',
    data: data
  })
}

export function updateSource(data) {
  return request({
    url: '/rules/source/update',
    method: 'put',
    data: data
  })
}

export function addSource(data) {
  return request({
    url: 'rules/source/add',
    method: 'put',
    data: data
  })
}
