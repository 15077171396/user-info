import { defineConfig } from '@hey-api/openapi-ts'

export default defineConfig({
  // 后端需先启动，SpringDoc 暴露的 OpenAPI 文档地址
  input: 'http://localhost:8080/v3/api-docs',
  output: 'src/api/generated',
  plugins: ['@hey-api/client-axios', '@hey-api/typescript', '@hey-api/sdk'],
})
