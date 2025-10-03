// layout.store.ts
import {
  signalStore,
  withState,
  withMethods,
  withComputed,
  withHooks,
  patchState,
  getState,
} from '@ngrx/signals';
import {
  LayoutState,
  initialState,
  LayoutType,
  LayoutMode,
  LayoutWidthType,
  LayoutPositionType,
  LayoutTopbarColor,
  SidebarSize,
  SidebarView,
  SidebarColor,
  SidebarImage,
  SidebarVisibility,
  PreloaderType,
  LayoutThemeColor,
  LayoutTheme,
} from './layout-types';
import { effect } from '@angular/core';

const STORAGE_KEY = 'app-layout-config';

function loadFromStorage(): LayoutState {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (raw) {
      return { ...initialState, ...JSON.parse(raw) };
    }
  } catch {
    // ignore parse errors
  }
  return initialState;
}

export const LayoutStore = signalStore(
  { providedIn: 'root' },

  withState<LayoutState>(loadFromStorage()),

  withMethods((store) => ({
    setLayoutType(type: LayoutType) {
      patchState(store, { layoutType: type });
    },
    setLayoutThemeColor(color: LayoutThemeColor){
      patchState(store, {layoutThemeColor: color})
    },
    setLayoutTheme(theme: LayoutTheme){
      patchState(store, {layoutTheme: theme})
    },
    setLayoutMode(mode: LayoutMode) {
      patchState(store, { layoutMode: mode });
    },
    setLayoutWidth(width: LayoutWidthType) {
      patchState(store, { layoutWidth: width });
    },
    setLayoutPosition(position: LayoutPositionType) {
      patchState(store, { layoutPosition: position });
    },
    setTopbarColor(color: LayoutTopbarColor) {
      patchState(store, { topbarColor: color });
    },
    setSidebarSize(size: SidebarSize) {
      patchState(store, { sidebarSize: size });
    },
    setSidebarView(view: SidebarView) {
      patchState(store, { sidebarView: view });
    },
    setSidebarColor(color: SidebarColor) {
      patchState(store, { sidebarColor: color });
    },
    setSidebarImage(image: SidebarImage) {
      patchState(store, { sidebarImage: image });
    },
    setSidebarVisibility(visibility: SidebarVisibility) {
      patchState(store, { sidebarVisibility: visibility });
    },
    setPreloader(preloader: PreloaderType) {
      patchState(store, { preloader });
    },
    reset() {
      patchState(store, initialState);
    },
  })),

  withComputed((store) => ({
    isDarkMode: () => store.layoutMode() === 'dark',
    isTwoColumn: () => store.layoutType() === 'twocolumn',
    isHorizontal: () => store.layoutType() === 'horizontal',
    isVertical: () => store.layoutType() === 'vertical',
    hasBoxedWidth: () => store.layoutWidth() === 'boxed',
    isFixed: () => store.layoutPosition() === 'fixed',
    isTopbarDark: () => store.topbarColor() === 'dark',
    isSidebarDetached: () => store.sidebarView() === 'detached',
    isSidebarSmall: () =>
      store.sidebarSize() === 'sm' || store.sidebarSize() === 'sm-hover',
    hasSidebarImage: () => store.sidebarImage() !== 'none',
    isPreloaderEnabled: () => store.preloader() === 'enable',
  })),

  withHooks({
    onInit(store) {
      // persist changes
      effect(() => {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(getState(store)));
      });
    },
  })
);
