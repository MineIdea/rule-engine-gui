import request from '@/utils/request'

export function listAlert(query) {
  return request({
    url: '/rules/alert/list',
    method: 'get',
    params: query
  })
}

export function delAlert(alertId) {
  return request({
    url: 'rules/alert/' + alertId,
    method: 'delete'
  })
}
