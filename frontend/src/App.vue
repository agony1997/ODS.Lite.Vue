<template>
  <q-layout view="hHh lpR fFf">
    <q-header elevated class="bg-primary text-white">
      <q-toolbar>
        <q-toolbar-title>Mock ODS VUE</q-toolbar-title>
        <q-btn flat to="/" label="首頁" />
        <q-btn flat to="/purchase-branch" label="訂貨門市" />
        <q-btn flat to="/purchase-sales" label="訂貨業務" />
      </q-toolbar>
    </q-header>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup>
</script>

<style>
</style>
