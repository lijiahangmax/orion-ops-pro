import type { FieldRule } from '@arco-design/web-vue';

export const groupId = [{
  message: '请选择分组'
}] as FieldRule[];

export const name = [{
  required: true,
  message: '请输入名称'
}, {
  maxLength: 64,
  message: '名称长度不能大于64位'
}] as FieldRule[];

export const command = [{
  required: true,
  message: '请输入代码片段'
}] as FieldRule[];

export default {
  groupId,
  name,
  command,
} as Record<string, FieldRule | FieldRule[]>;
