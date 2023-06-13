import request from '@/utils/request'

export function listSource(query) {
  return request({
    url: '/rules/source/list',
    method: 'get',
    params: query
  })
}
