/**
 * 将 0x 格式的颜色值转换为标准的 # 十六进制颜色值
 * @param input - 输入的颜色值（如 0xFF0000、0x080e0）
 * @returns 标准的 # 格式颜色值（如 #FF0000）
 */
export function toHexColor(input: string): string {
  if (!input) return '#000000'
  
  // 去掉 0x 前缀
  const colorValue = input.startsWith('0x') || input.startsWith('0X') 
    ? input.slice(2) 
    : input
  
  // 将剩余部分解析为十六进制数，再转成 6 位十六进制字符串
  const hexColor = parseInt(colorValue, 16).toString(16).padStart(6, '0')
  
  // 返回标准 #RRGGBB 格式
  return `#${hexColor.toUpperCase()}`
}

/**
 * 将 # 格式的颜色值转换为 0x 格式
 * @param hexColor - # 格式的颜色值（如 #FF0000）
 * @returns 0x 格式的颜色值（如 0xFF0000）
 */
export function toOxColor(hexColor: string): string {
  if (!hexColor) return '0x000000'
  
  // 去掉 # 前缀
  const cleanHex = hexColor.startsWith('#') ? hexColor.slice(1) : hexColor
  
  // 返回 0x 格式
  return `0x${cleanHex.toUpperCase()}`
}

