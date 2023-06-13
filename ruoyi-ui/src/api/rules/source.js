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
