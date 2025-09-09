export type CategoryKey = 'tech' | 'question' | 'share' | 'project'

export const categoryOptions: Array<{ value: CategoryKey; label: string; color: string }> = [
  { value: 'tech', label: '技术交流', color: 'blue' },
  { value: 'question', label: '问答求助', color: 'orange' },
  { value: 'share', label: '经验分享', color: 'purple' },
  { value: 'project', label: '项目', color: 'green' },
]

const nameMap: Record<string, string> = Object.fromEntries(
  categoryOptions.map((c) => [c.value, c.label]),
)

const colorMap: Record<string, string> = Object.fromEntries(
  categoryOptions.map((c) => [c.value, c.color]),
)

export const getCategoryName = (category?: string) => {
  const key = (category || '').toLowerCase()
  return nameMap[key] || '其他'
}

export const getCategoryColor = (category?: string) => {
  const key = (category || '').toLowerCase()
  return colorMap[key] || 'default'
}


