import type { RadioOption } from '@arco-design/web-vue/es/radio/interface';
import type { DictState } from './types';
import type { Options } from '@/types/global';
import { defineStore } from 'pinia';
import { getDictValueList } from '@/api/system/dict-value';

export default defineStore('dict', {
  state: (): DictState => ({}),

  actions: {
    // 加载字典值
    async loadKeys(keys: string[]) {
      // 检查是否存在
      const unloadKeys = keys.filter(key => !this.$state.hasOwnProperty(key));
      if (!unloadKeys.length) {
        return;
      }
      // 加载未加载的数据
      try {
        const { data } = await getDictValueList(unloadKeys);
        this.$patch(data as object);
      } catch (e) {
      } finally {
      }
    },

    // 获取字典选项
    toOptions(key: string) {
      return this.$state[key];
    },

    // 获取字典选项
    toRadioOptions(key: string) {
      return this.$state[key] as RadioOption[];
    },

    // 获取字典值
    getDictValue(dict: string,
                 value: any,
                 key = 'label',
                 defaultValue = value) {
      for (let dictValue of this.$state[dict] || []) {
        if (dictValue.value === value) {
          return dictValue[key];
        }
      }
      return defaultValue;
    },

    // 获取字典值对象
    getDict(dict: string, value: any): Options {
      for (let dictValue of this.$state[dict] || []) {
        if (dictValue.value === value) {
          return dictValue;
        }
      }
      return {
        value
      } as Options;
    },

    // 切换字典值
    toggleDictValue(dict: string,
                    value: any,
                    key = 'value',
                    defaultValue = value) {
      for (let dictValue of this.$state[dict] || []) {
        if (dictValue.value !== value) {
          return dictValue[key];
        }
      }
      return defaultValue;
    },

    // 切换字典值对象
    toggleDict(dict: string, value: any): Options {
      for (let dictValue of this.$state[dict] || []) {
        if (dictValue.value !== value) {
          return dictValue;
        }
      }
      return {} as Options;
    }

  },
});
