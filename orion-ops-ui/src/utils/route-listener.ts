import type { RouteLocationNormalized } from 'vue-router';
import type { Handler } from 'mitt';
import mitt from 'mitt';

const emitter = mitt();

const key = Symbol('ROUTE_CHANGE');

let latestRoute: RouteLocationNormalized;

export function setRouteEmitter(to: RouteLocationNormalized) {
  emitter.emit(key, to);
  latestRoute = to;
}

/**
 * 添加路由跳转监听器
 */
export function listenerRouteChange(
  handler: (route: RouteLocationNormalized) => void,
  immediate = true
) {
  emitter.on(key, handler as Handler);
  if (immediate && latestRoute) {
    handler(latestRoute);
  }
}

/**
 * 移除路由跳转监听器
 */
export function removeRouteListener() {
  emitter.off(key);
}
