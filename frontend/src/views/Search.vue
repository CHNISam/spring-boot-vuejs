<template>
    <div class="search-view">
        <h2>Results for “{{ q }}”</h2>
        <div v-if="loading">Loading…</div>
        <ul v-else>
            <li v-for="p in results" :key="p.id">
                <h4>{{ p.title }}</h4>
                <p>{{ p.content }}</p>
            </li>
            <li v-if="!results.length">No results.</li>
        </ul>
    </div>
</template>

<script lang="ts">
import api, { Post } from '@/api/backend-api'
import { defineComponent, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'

export default defineComponent({
    name: 'SearchView',
    setup() {
        const route = useRoute()
        const q = ref<string>(route.query.q as string || '')
        const results = ref<Post[]>([])
        const loading = ref(false)

        async function doSearch() {
            console.log('🔍 Searching for q=', q.value);
            loading.value = true;
            try {
                const res = await api.searchPosts(q.value);
                console.log('👀 searchPosts response:', res.data);
                results.value = res.data;
            } catch (e) {
                console.error('Search error:', e);
                results.value = [];
            } finally {
                loading.value = false;
            }
        }


        watch(() => route.query.q, val => {
            q.value = (val as string) || ''
            doSearch()
        })

        onMounted(doSearch)
        return { q, results, loading }
    }
})
</script>

<style scoped>
.search-view {
    max-width: 800px;
    margin: 0 auto;
    padding: 24px;
    color: #ddd;
}

.search-view ul {
    list-style: none;
    padding: 0;
}

.search-view li {
    margin-bottom: 16px;
    padding-bottom: 8px;
    border-bottom: 1px solid #303030;
}
</style>
