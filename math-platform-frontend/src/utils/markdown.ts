import { marked } from 'marked'
import hljs from 'highlight.js'
import DOMPurify from 'dompurify'
import katex from 'katex'

// 配置 marked：启用 GFM、代码高亮可后续接入
marked.setOptions({
  gfm: true,
  breaks: true,
  highlight(code, lang) {
    try {
      if (lang && hljs.getLanguage(lang)) {
        return hljs.highlight(code, { language: lang }).value
      }
      return hljs.highlightAuto(code).value
    } catch {
      return code
    }
  },
})

// 将 markdown 转为安全 HTML，并渲染 $$...$$ 与 $...$ 的 KaTeX 公式
export function renderMarkdownWithKatex(md: string): string {
  const html = marked.parse(md || '') as string
  const withBlockMath = html.replace(/\$\$([\s\S]+?)\$\$/g, (_, expr) =>
    katex.renderToString(expr, { throwOnError: false, displayMode: true }),
  )
  const withInlineMath = withBlockMath.replace(/(?<!\$)\$([^\n$]+?)\$(?!\$)/g, (_, expr) =>
    katex.renderToString(expr, { throwOnError: false, displayMode: false }),
  )
  return DOMPurify.sanitize(withInlineMath)
}


