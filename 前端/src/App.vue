<template>

<Navbar/>

<div class="app-container">
  <router-view/>
</div>

</template>

<script setup>
import Navbar from "./components/Navbar.vue"
</script>

<style>

.app-container{
padding-top:60px;   /* Navbar高度 */
}

</style>