// layout.types.ts

export type LayoutType = 'vertical' | 'horizontal' | 'twocolumn' | 'semibox';
export type LayoutTheme = "default" | "saas" |"corporate" | "galaxy" | "material"|"creative"| "minimal"| "modern"| "interactive"| "classic"|"vintage";
export type LayoutThemeColor = 'default' | 'green' | 'purple' | 'blue';
export type LayoutMode = 'light' | 'dark';

export type LayoutWidthType = 'fluid' | 'boxed';

export type LayoutPositionType = 'fixed' | 'scrollable';

export type LayoutTopbarColor = 'light' | 'dark';

export type SidebarSize = 'lg' | 'md' | 'sm' | 'sm-hover';

export type SidebarView = 'default' | 'detached';

export type SidebarColor =
  | 'light'
  | 'dark'
  | 'gradient'
  | 'gradient-2'
  | 'gradient-3'
  | 'gradient-4';

export type SidebarImage = 'none' | 'img-1' | 'img-2' | 'img-3' | 'img-4';

export type SidebarVisibility = 'show' | 'hidden';

export type PreloaderType = 'enable' | 'disable';


export interface LayoutState {
  layoutType: LayoutType;
  layoutTheme: LayoutTheme;
  layoutThemeColor: LayoutThemeColor;
  layoutMode: LayoutMode;
  layoutWidth: LayoutWidthType;
  layoutPosition: LayoutPositionType;
  topbarColor: LayoutTopbarColor;
  sidebarSize: SidebarSize;
  sidebarView: SidebarView;
  sidebarColor: SidebarColor;
  sidebarImage: SidebarImage;
  sidebarVisibility: SidebarVisibility;
  preloader: PreloaderType;
}

// default values
export const initialState: LayoutState = {
  layoutType: 'vertical',
  layoutTheme: 'default',
  layoutThemeColor: 'default',
  layoutMode: 'light',
  layoutWidth: 'fluid',
  layoutPosition: 'fixed',
  topbarColor: 'light',
  sidebarSize: 'lg',
  sidebarView: 'default',
  sidebarColor: 'light',
  sidebarImage: 'none',
  sidebarVisibility: 'show',
  preloader: 'disable',
};
