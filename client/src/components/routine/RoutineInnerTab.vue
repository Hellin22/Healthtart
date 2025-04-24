<template>
    <div class="container">
        <nav class="inner-tab">
            <div class="nav-menu">
                <button :class="{'active-button': activeTab === 'ai', 'inactive-button': activeTab !== 'ai'}"@click="setActiveTab('ai')">AI 생성 루틴</button>
            </div>
            <div class="nav-menu">
                <button :class="{'active-button': activeTab === 'user', 'inactive-button': activeTab !== 'user'}"@click="setActiveTab('user')">유저 추천 루틴</button>
            </div>
        </nav>  
        <div :class="{'main-container': true, 'user-container': activeTab === 'user'}">
            <slot></slot>
        </div>
    </div>
</template>

<script setup>
    import '@/assets/css/routine/RoutineInnerTab.css';
    import { ref, watch, onMounted } from 'vue';
    import { useRouter, useRoute } from 'vue-router';

    const activeTab = ref('ai'); 
    const router = useRouter();
    const route = useRoute();

    const setActiveTab = (tab) => {
        activeTab.value = tab;
        switch(tab){
            case 'ai':
                router.push({ path: '/routine' });
                break;
            case 'user':
                router.push({ path: '/user-recommendation' });
                break;
        }
    };

    function updateActiveTabFromRoute() {
        const path = route.path;
        if (path === '/routine') {
            activeTab.value = 'ai';
        } else if (path ==='/user-recommendation') {
            activeTab.value = 'user';
        }
    }

    onMounted(() => {
    updateActiveTabFromRoute();
    });

    watch(route, () => {
        updateActiveTabFromRoute();
    });
 </script>
