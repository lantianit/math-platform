/**
 * 企业级设计Token系统
 * 统一管理所有设计元素，确保视觉一致性
 */

export interface DesignTokens {
  colors: {
    primary: Record<string, string>
    semantic: Record<string, string>
    neutral: Record<string, string>
    surface: Record<string, string>
  }
  typography: {
    fontFamily: Record<string, string>
    fontSize: Record<string, string>
    fontWeight: Record<string, number>
    lineHeight: Record<string, number>
  }
  spacing: Record<string, string>
  borderRadius: Record<string, string>
  shadows: Record<string, string>
  transitions: Record<string, string>
}

export const designTokens: DesignTokens = {
  colors: {
    primary: {
      50: '#f0f9ff',
      100: '#e0f2fe',
      200: '#bae6fd',
      300: '#7dd3fc',
      400: '#38bdf8',
      500: '#0ea5e9',
      600: '#0284c7',
      700: '#0369a1',
      800: '#075985',
      900: '#0c4a6e',
    },
    semantic: {
      success: '#10b981',
      warning: '#f59e0b',
      error: '#ef4444',
      info: '#3b82f6',
    },
    neutral: {
      50: '#fafafa',
      100: '#f5f5f5',
      200: '#e5e5e5',
      300: '#d4d4d4',
      400: '#a3a3a3',
      500: '#737373',
      600: '#525252',
      700: '#404040',
      800: '#262626',
      900: '#171717',
    },
    surface: {
      background: '#ffffff',
      card: '#ffffff',
      elevated: '#ffffff',
      overlay: 'rgba(0, 0, 0, 0.5)',
    },
  },
  typography: {
    fontFamily: {
      primary: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif',
      mono: '"SF Mono", Monaco, "Cascadia Code", "Roboto Mono", Consolas, "Courier New", monospace',
    },
    fontSize: {
      xs: '0.75rem',    // 12px
      sm: '0.875rem',   // 14px
      base: '1rem',     // 16px
      lg: '1.125rem',   // 18px
      xl: '1.25rem',    // 20px
      '2xl': '1.5rem',  // 24px
      '3xl': '1.875rem', // 30px
      '4xl': '2.25rem', // 36px
    },
    fontWeight: {
      normal: 400,
      medium: 500,
      semibold: 600,
      bold: 700,
    },
    lineHeight: {
      tight: 1.25,
      normal: 1.5,
      relaxed: 1.75,
    },
  },
  spacing: {
    1: '0.25rem',   // 4px
    2: '0.5rem',    // 8px
    3: '0.75rem',   // 12px
    4: '1rem',      // 16px
    5: '1.25rem',   // 20px
    6: '1.5rem',    // 24px
    8: '2rem',      // 32px
    10: '2.5rem',   // 40px
    12: '3rem',     // 48px
    16: '4rem',     // 64px
    20: '5rem',     // 80px
    24: '6rem',     // 96px
  },
  borderRadius: {
    none: '0',
    sm: '0.125rem',   // 2px
    md: '0.375rem',   // 6px
    lg: '0.5rem',     // 8px
    xl: '0.75rem',    // 12px
    '2xl': '1rem',    // 16px
    full: '9999px',
  },
  shadows: {
    none: 'none',
    sm: '0 1px 2px 0 rgba(0, 0, 0, 0.05)',
    md: '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)',
    lg: '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)',
    xl: '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)',
    '2xl': '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
  },
  transitions: {
    fast: '150ms cubic-bezier(0.4, 0, 0.2, 1)',
    normal: '250ms cubic-bezier(0.4, 0, 0.2, 1)',
    slow: '350ms cubic-bezier(0.4, 0, 0.2, 1)',
  },
}

/**
 * 生成CSS自定义属性
 */
export const generateCSSVariables = (tokens: DesignTokens): Record<string, string> => {
  const variables: Record<string, string> = {}
  
  // 颜色变量
  Object.entries(tokens.colors.primary).forEach(([key, value]) => {
    variables[`--color-primary-${key}`] = value
  })
  
  Object.entries(tokens.colors.semantic).forEach(([key, value]) => {
    variables[`--color-${key}`] = value
  })
  
  Object.entries(tokens.colors.neutral).forEach(([key, value]) => {
    variables[`--color-neutral-${key}`] = value
  })
  
  Object.entries(tokens.colors.surface).forEach(([key, value]) => {
    variables[`--color-surface-${key}`] = value
  })
  
  // 间距变量
  Object.entries(tokens.spacing).forEach(([key, value]) => {
    variables[`--spacing-${key}`] = value
  })
  
  // 圆角变量
  Object.entries(tokens.borderRadius).forEach(([key, value]) => {
    variables[`--radius-${key}`] = value
  })
  
  // 阴影变量
  Object.entries(tokens.shadows).forEach(([key, value]) => {
    variables[`--shadow-${key}`] = value
  })
  
  // 过渡变量
  Object.entries(tokens.transitions).forEach(([key, value]) => {
    variables[`--transition-${key}`] = value
  })
  
  return variables
}

/**
 * 应用设计Token到文档根元素
 */
export const applyDesignTokens = () => {
  const variables = generateCSSVariables(designTokens)
  const root = document.documentElement
  
  Object.entries(variables).forEach(([property, value]) => {
    root.style.setProperty(property, value)
  })
}
