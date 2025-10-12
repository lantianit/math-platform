// 空间级别枚举
export const NOTE_SPACE_LEVEL_ENUM = {
  COMMON: 0,
  PROFESSIONAL: 1,
  FLAGSHIP: 2,
} as const

// 空间级别文本映射
export const NOTE_SPACE_LEVEL_MAP: Record<number, string> = {
  0: '普通版',
  1: '专业版',
  2: '旗舰版',
}

// 空间级别选项
export const NOTE_SPACE_LEVEL_OPTIONS = Object.keys(NOTE_SPACE_LEVEL_MAP).map((key) => {
  const value = Number(key)
  return {
    label: NOTE_SPACE_LEVEL_MAP[value],
    value,
  }
})

// 格式化文件大小
export const formatSize = (bytes: number): string => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return (bytes / Math.pow(k, i)).toFixed(2) + ' ' + sizes[i]
}

