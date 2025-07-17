<template>
  <div class="skeleton-card">
    <div class="skeleton-header">
      <div class="skeleton-avatar"></div>
      <div class="skeleton-meta">
        <div class="skeleton-line short"></div>
        <div class="skeleton-line shorter"></div>
      </div>
    </div>
    <div class="skeleton-body">
      <div class="skeleton-line long"></div>
      <div class="skeleton-line medium"></div>
    </div>
    <div class="skeleton-footer">
      <div class="skeleton-icon"></div>
      <div class="skeleton-icon"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
// Static skeleton component, no logic required
</script>

<style scoped>
.skeleton-card {
  background: var(--card-bg);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: var(--gap);
  display: flex;
  flex-direction: column;
  animation: pulse 1.5s ease-in-out infinite;
}
.skeleton-header,
.skeleton-body,
.skeleton-footer {
  display: flex;
  align-items: center;
  margin-bottom: var(--gap);
}
.skeleton-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--border);
}
.skeleton-meta {
  flex: 1;
  margin-left: var(--gap);
}
.skeleton-line {
  height: 12px;
  background: var(--border);
  border-radius: 4px;
  margin-bottom: 8px;
}
.skeleton-line.short { width: 50%; }
.skeleton-line.shorter { width: 30%; }
.skeleton-line.long { width: 100%; }
.skeleton-line.medium { width: 70%; }
.skeleton-footer .skeleton-icon {
  width: 16px;
  height: 16px;
  background: var(--border);
  border-radius: 4px;
  margin-right: var(--gap);
}
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50%      { opacity: 0.4; }
}
</style>
