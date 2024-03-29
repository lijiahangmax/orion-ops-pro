import type { RouteRecordNormalized, RouteRecordRaw } from 'vue-router';
import { computed } from 'vue';
import { useMenuStore } from '@/store';
import { cloneDeep } from 'lodash';

export default function useMenuTree() {
  const menuStore = useMenuStore();
  const appRoute = computed(() => {
    return menuStore.appMenus;
  });
  const menuTree = computed<RouteRecordNormalized[]>(() => {
    const copyRouter = cloneDeep(appRoute.value) as RouteRecordNormalized[];

    // 排序
    copyRouter.sort((a: RouteRecordNormalized, b: RouteRecordNormalized) => {
      return (a.meta.order || 0) - (b.meta.order || 0);
    });

    function travel(_routes: RouteRecordRaw[], layer: number) {
      if (!_routes) return null;

      const collector: any = _routes.map((element) => {
        // 隐藏子目录
        if (element.meta?.hideInMenu || !element.children) {
          element.children = [];
          if (element.meta?.hideInMenu) {
            // 如果隐藏菜单 则不显示
            return null;
          } else {
            return element;
          }
        }

        // 过滤不显示的菜单
        element.children = element.children.filter(
          (x) => x.meta?.hideInMenu !== true
        );

        // 关联子节点
        const subItem = travel(element.children, layer + 1);

        if (subItem.length) {
          element.children = subItem;
          return element;
        }
        // 第二层 (子目录)
        if (layer > 1) {
          element.children = subItem;
          return element;
        }

        // 是否隐藏目录
        if (element.meta?.hideInMenu === false) {
          return element;
        }
        return null;
      });
      return collector.filter(Boolean);
    }

    return travel(copyRouter, 0);
  });

  return {
    menuTree,
  };
}
