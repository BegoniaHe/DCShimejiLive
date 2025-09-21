import { defineConfig } from 'vitepress'

export default defineConfig({
  title: 'Shimeji-Live',
  description: '一个桌面吉祥物应用程序，让可爱的角色在您的屏幕上自由活动',
  lang: 'zh-CN',
  
  // 忽略死链接检查，在开发阶段很有用
  ignoreDeadLinks: true,
  
  head: [
    ['link', { rel: 'icon', href: '/icon.ico' }],
    ['link', { rel: 'icon', type: 'image/png', href: '/icon.png' }],
    ['meta', { name: 'theme-color', content: '#3c82f6' }]
  ],

  locales: {
    root: {
      label: '简体中文',
      lang: 'zh-CN',
      title: 'Shimeji-Live',
      description: '桌面吉祥物应用程序文档'
    },
    en: {
      label: 'English',
      lang: 'en-US',
      title: 'Shimeji-Live',
      description: 'Desktop mascot application documentation'
    }
  },

  themeConfig: {
    logo: '/icon.png',
    socialLinks: [
      { icon: 'github', link: 'https://github.com/BegoniaHe/dc-ShimejiLive' }
    ],
    search: {
      provider: 'local',
      options: {
        locales: {
          zh: {
            translations: {
              button: { buttonText: '搜索文档', buttonAriaLabel: '搜索文档' },
              modal: {
                noResultsText: '无法找到相关结果',
                resetButtonTitle: '清除查询条件',
                footer: { selectText: '选择', navigateText: '切换' }
              }
            }
          },
          en: {
            translations: {
              button: { buttonText: 'Search Docs', buttonAriaLabel: 'Search Docs' },
              modal: {
                noResultsText: 'No results found',
                resetButtonTitle: 'Reset search',
                footer: { selectText: 'to select', navigateText: 'to navigate' }
              }
            }
          }
        }
      }
    },
    locales: {
      root: {
        label: '简体中文',
        lang: 'zh-CN',
        nav: [
          { text: '首页', link: '/' },
          { text: '用户指南', link: '/user/install' },
          { text: '开发文档', link: '/development/getting-started' },
          { text: 'GitHub', link: 'https://github.com/DCRepairCenter/ShimejiLive' }
        ],
        sidebar: {
          '/user/': [
            {
              text: '用户指南',
              items: [
                { text: '安装指南', link: '/user/install' },
                { text: '自定义角色', link: '/user/customization' },
                {
                  text: '教程',
                  collapsed: false,
                  items: [
                    { text: '入门介绍', link: '/user/tutorial/introduction' },
                    { text: '动作基础', link: '/user/tutorial/actions-foundation' },
                    { text: '行为模式', link: '/user/tutorial/behavior-patterns' },
                    { text: '个性化定制', link: '/user/tutorial/personality-and-mods' },
                    { text: '高级互动', link: '/user/tutorial/advanced-interaction' },
                    { text: '逐帧动画解析', link: '/user/tutorial/frame-analysis' },
                    { text: '自定义开发', link: '/user/tutorial/custom-development' }
                  ]
                }
              ]
            }
          ],
          '/development/': [
            {
              text: '开发文档',
              items: [
                { text: '开发指南', link: '/development/getting-started' },
                { text: '动作系统', link: '/development/actions' },
                { text: '脚本系统', link: '/development/scripting' },
                { text: '声音系统', link: '/development/sound' },
                { text: '架构概览', link: '/development/architecture' },
                { text: '配置系统', link: '/development/configuration' },
                { text: '国际化(i18n)', link: '/development/i18n' },
                { text: '构建部署', link: '/development/build-deploy' },
                { text: '开发技巧', link: '/development/tips' }
              ]
            }
          ]
        },
        footer: {
          message: '基于 zlib License 发布',
          copyright: '版权所有 © 2009-2025 Group Finity & Shimeji-ee Group'
        },
        editLink: {
          pattern: 'https://github.com/DCRepairCenter/ShimejiLive/edit/main/docs-site/:path',
          text: '在 GitHub 上编辑此页'
        },
        lastUpdated: {
          text: '最后更新于'
        }
      },
      en: {
        label: 'English',
        lang: 'en-US',
        nav: [
          { text: 'Home', link: '/en/' },
          { text: 'User Guide', link: '/en/user/install' },
          { text: 'Developer Docs', link: '/en/development/getting-started' },
          { text: 'GitHub', link: 'https://github.com/DCRepairCenter/ShimejiLive' }
        ],
        sidebar: {
          '/en/user/': [
            {
              text: 'User Guide',
              items: [
                { text: 'Installation', link: '/en/user/install' },
                { text: 'Customization', link: '/en/user/customization' },
                { text: 'Tutorials', link: '/en/user/tutorial/' }
              ]
            }
          ],
          '/en/development/': [
            {
              text: 'Developer Docs',
              items: [
                { text: 'Getting Started', link: '/en/development/getting-started' },
                { text: 'Action System', link: '/en/development/actions' },
                { text: 'Architecture', link: '/en/development/architecture' },
                { text: 'Configuration', link: '/en/development/configuration' },
                { text: 'Internationalization (i18n)', link: '/en/development/i18n' },
                { text: 'Build & Deploy', link: '/en/development/build-deploy' },
                { text: 'Tips', link: '/en/development/tips' }
              ]
            }
          ]
        },
        footer: {
          message: 'Released under the zlib License.',
          copyright: 'Copyright © 2009-2025 Group Finity & Shimeji-ee Group'
        },
        editLink: {
          pattern: 'https://github.com/DCRepairCenter/ShimejiLive/edit/main/docs-site/:path',
          text: 'Edit this page on GitHub'
        },
        lastUpdated: {
          text: 'Last Updated'
        }
      }
    }
  },

  vite: {
    publicDir: '../img'
  }
})
