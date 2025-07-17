import type { Store } from 'vuex';
import { useStore as baseUseStore } from 'vuex';
import type { State } from './index'; // 你的 store 类型文件

export function useStore(): Store<State> {
  return baseUseStore<State>();
}
