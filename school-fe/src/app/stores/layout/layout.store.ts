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
    setLayoutThemeColor(color: LayoutThemeColor) {
      patchState(store, { layoutThemeColor: color })
    },
    setLayoutTheme(theme: LayoutTheme) {
      patchState(store, { layoutTheme: theme })
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
    isSemibox: () => store.layoutType() === 'semibox',
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
        const state = getState(store)
        localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
        applyDomAttributes(state)
      });
    },
  })
);


function applyDomAttributes(data: LayoutState) {
  document.documentElement.setAttribute('data-layout', data.layoutType);

  document.documentElement.setAttribute('data-theme', data.layoutTheme);
  document.documentElement.setAttribute('data-theme-colors', data.layoutThemeColor);
  document.documentElement.setAttribute('data-bs-theme', data.layoutMode);
  document.documentElement.setAttribute('data-layout-width', data.layoutWidth);
  document.documentElement.setAttribute('data-layout-position', data.layoutPosition);
  document.documentElement.setAttribute('data-topbar', data.topbarColor);
  data.layoutType == "vertical" || data.layoutType == "twocolumn" ? document.documentElement.setAttribute('data-sidebar', data.sidebarColor) : '';
  data.layoutType == "vertical" || data.layoutType == "twocolumn" ? document.documentElement.setAttribute('data-sidebar-size', data.sidebarSize) : '';
  data.layoutType == "vertical" || data.layoutType == "twocolumn" ? document.documentElement.setAttribute('data-sidebar-image', data.sidebarImage) : '';
  data.layoutType == "vertical" || data.layoutType == "twocolumn" ? document.documentElement.setAttribute('data-layout-style', data.sidebarView) : '';
  document.documentElement.setAttribute('data-preloader', data.preloader)
  document.documentElement.setAttribute('data-sidebar-visibility', data.sidebarVisibility);
}
