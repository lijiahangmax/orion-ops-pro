import { defineStore } from 'pinia';
import { CacheState } from './types';

export type CacheType = 'menus' | 'roles' | 'tags' | 'hostKeys' | 'hostIdentities'

const useCacheStore = defineStore('cache', {
  state: (): CacheState => ({
    menus: [],
    roles: [],
    tags: [],
    hostKeys: [],
    hostIdentities: [],
  }),

  getters: {},

  actions: {
    /**
     * 设置
     */
    set(name: CacheType, value: any) {
      this[name] = value;
    }
  },
});

export default useCacheStore;
