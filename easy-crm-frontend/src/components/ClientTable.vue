<script setup lang="ts">
import { computed, ref } from 'vue'
import BaseHeader from '@/components/BaseHeader.vue'
import { useKeycloak } from '@dsb-norge/vue-keycloak-js'

const kc = useKeycloak()

fetch(import.meta.env.VITE_API_BASE_URL + '/clients', {
  headers: {
    Authorization: `Bearer ${kc.token}`,
  },
})
  .then((result) => result.json())
  .then((result: Client[]) => (clients.value = result))

export interface Client {
  id: string
  firstName: string | null
  lastName: string | null
  company: string | null
  email: string
  createdAt: string
  city: string
  zip: string
}

interface Column {
  key: keyof Client
  label: string
}

const columns: Column[] = [
  { key: 'firstName', label: 'Vorname' },
  { key: 'lastName', label: 'Nachname' },
  { key: 'company', label: 'Firma' },
  { key: 'city', label: 'Stadt' },
  { key: 'zip', label: 'PLZ' },
  { key: 'email', label: 'E-Mail' },
]

const clients = ref<Client[]>([])

const sortSource = ref<{ key: keyof Client; direction: 'asc' | 'desc' }>({
  key: 'zip',
  direction: 'asc',
})
const sorting = ref<(c1: Client, c2: Client) => number>((c1, c2) => c1.zip.localeCompare(c2.zip))

const filter = ref<(client: Client) => boolean>(() => true)

const sortedFilteredClients = computed<Client[]>(() =>
  clients.value.filter(filter.value).toSorted(sorting.value),
)

const toggleSort = (key: keyof Client) => {}

const mails = computed(() => sortedFilteredClients.value.map((client) => client.email).join(','))

const updateFilter = (event: Event) => {
  const isChecked = (event.target as HTMLInputElement).checked

  filter.value = isChecked ? (client) => /^[456].*/g.test(client.zip) : () => true
}

const mailTo = computed(() => `mailto:mail@bdo-agentur.de?bcc=${mails.value}`)

const openMail = () => window.open(mailTo.value)
const copyMails = () => navigator.clipboard.writeText(mails.value)

const visibleClientsCount = computed(() => sortedFilteredClients.value.length)
</script>

<template>
  <div class="flex h-full flex-col">
    <BaseHeader :visible-client-count="visibleClientsCount" />
    <div class="flex min-h-0 grow flex-col gap-7 p-5">
      <div class="min-h-0 grow overflow-auto border border-slate-500">
        <table class="w-full table-fixed">
          <thead>
            <tr>
              <th
                class="sticky top-0 bg-gray-300 p-2"
                v-for="column in columns"
                :key="column.key"
                @click="toggleSort(column.key)"
              >
                <div class="flex items-center gap-3">
                  <span>{{ column.label }}</span>

                  <div class="w-4">
                    {{
                      sortSource.key === column.key
                        ? { asc: ' 🔼', desc: ' 🔽' }[sortSource.direction]
                        : undefined
                    }}
                  </div>

                  <template v-if="column.key === 'zip'">
                    <input
                      @click="(event) => event.stopPropagation()"
                      @input="(event) => updateFilter(event)"
                      type="checkbox"
                    />
                    Nur 4*,5*,6*
                  </template>
                </div>
              </th>
            </tr>
          </thead>

          <tbody>
            <tr
              class="border-b border-b-gray-200"
              v-for="client in sortedFilteredClients"
              :key="client.id"
            >
              <td class="p-2" v-for="column in columns" :key="column.key">
                <span>{{ client[column.key] }}</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="flex gap-5">
        <button @click="openMail" class="rounded border border-slate-500 bg-slate-400 p-3">
          Email an sichtbare Adressen senden
        </button>
        <button @click="copyMails" class="rounded border border-slate-500 bg-slate-300 p-3">
          Sichtbare Adressen kopieren
        </button>
      </div>
    </div>
  </div>
</template>
