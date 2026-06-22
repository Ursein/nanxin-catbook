<script setup>
</script>

<template>
  <div class="about-page">
    <section class="about-hero">
      <div class="container">
        <span class="eyebrow">关于</span>
        <h1 class="about-title">南信猫友记</h1>
        <p class="about-sub">NanXin CatBook — 校园流浪猫信息管理与推荐系统</p>
      </div>
    </section>

    <section class="about-content">
      <div class="container">
        <div class="content-grid">
          <div class="content-card">
            <h2>项目介绍</h2>
            <p>南信猫友记是一个面向南京信息工程大学校园流浪猫咪的信息管理与推荐系统，帮助师生了解和关爱校园猫咪。</p>
          </div>
          <div class="content-card">
            <h2>技术栈</h2>
            <p>后端 Spring Boot 3.2.5 + Java 17 · 前端 Vue 3 + Vite · 数据库 MySQL 8</p>
          </div>
          <div class="content-card">
            <h2>核心算法</h2>
            <p>基于余弦相似度的内容推荐算法，融合点赞、关注、评分等热度因子，在猫咪详情页推荐相似猫咪。</p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.about-page {
  padding: var(--section-py) 0;
}

.about-hero {
  margin-bottom: 3rem;
}

.about-title {
  font-size: clamp(2.5rem, 5vw, 4rem);
  font-weight: 800;
  letter-spacing: -0.03em;
  margin: 0.75rem 0 1rem;
}

.about-sub {
  font-size: 1.125rem;
  color: var(--text-secondary);
  max-width: 65ch;
  line-height: 1.7;
}

.content-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1rem;
}

.content-card {
  padding: 1.5rem;
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-md);
}

.content-card h2 {
  font-size: 1rem;
  font-weight: 700;
  margin-bottom: 0.75rem;
  color: var(--text-secondary);
}

.content-card p {
  font-size: 0.9375rem;
  color: var(--text-secondary);
  line-height: 1.7;
}
</style>