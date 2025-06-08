<script setup lang="ts">
import {
  createColumnHelper,
  FlexRender,
  getCoreRowModel,
  getFilteredRowModel,
  getSortedRowModel,
  type SortingState,
  useVueTable,
} from '@tanstack/vue-table'
import { computed, ref } from 'vue'
import BaseHeader from '@/components/BaseHeader.vue'
import { useKeycloak } from '@dsb-norge/vue-keycloak-js'

const kc = useKeycloak()

fetch(import.meta.env.VITE_API_BASE_URL + '/clients', {
  headers: {
    Authorization: `Bearer ${kc.token}`,
  },
}).then((result) => console.log(result))

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

const columnHelper = createColumnHelper<Client>()

const columns = [
  columnHelper.accessor('firstName', {
    header: 'Vorname',
    enableColumnFilter: false,
  }),
  columnHelper.accessor('lastName', {
    header: 'Nachname',
    enableColumnFilter: false,
  }),
  columnHelper.accessor('company', {
    header: 'Firma',
    enableColumnFilter: false,
  }),
  columnHelper.accessor('city', {
    header: 'Stadt',
    enableColumnFilter: false,
  }),
  columnHelper.accessor('zip', {
    header: 'PLZ',
    enableColumnFilter: true,
    filterFn: (row, id, filterRegex: RegExp) => {
      console.log(filterRegex)
      return filterRegex.test(row.getValue(id)!.toString())
    },
  }),
  columnHelper.accessor('email', {
    header: 'E-Mail',
    enableColumnFilter: false,
  }),
]

const clients = ref<Client[]>([])
const sorting = ref<SortingState>([])

const table = useVueTable({
  get data() {
    return clients.value
  },
  columns,
  state: {
    get sorting() {
      return sorting.value
    },
  },
  onSortingChange: (updaterOrValue) => {
    sorting.value =
      typeof updaterOrValue === 'function' ? updaterOrValue(sorting.value) : updaterOrValue
  },
  getCoreRowModel: getCoreRowModel(),
  getSortedRowModel: getSortedRowModel(),
  getFilteredRowModel: getFilteredRowModel(),
  debugTable: true,
  initialState: {
    sorting: [
      {
        id: 'zip',
        desc: false,
      },
    ],
  },
})

const mails = computed(() =>
  table
    .getFilteredRowModel()
    .rows.map((row) => row.getValue('email'))
    .join(','),
)

const updateFilter = (event: Event) => {
  console.log((event.target as HTMLInputElement).checked)
  setPLZFilter(/^[456].*/g)
}

const mailTo = computed(() => `mailto:mail@bdo-agentur.de?bcc=${mails.value}`)

const openMail = () => window.open(mailTo.value)
const copyMails = () => navigator.clipboard.writeText(mails.value)

const setPLZFilter = (filter: RegExp) => {
  table.getColumn('zip')?.setFilterValue(filter)
}

const visibleClientsCount = computed(() => table.getRowModel().rows.length)
</script>

<template>
  <div class="flex h-full flex-col">
    <BaseHeader :visible-client-count="visibleClientsCount" />
    <div class="flex h-full flex-col gap-7 p-5">
      <div class="min-h-0 grow overflow-auto border border-slate-500">
        <table class="w-full table-fixed">
          <thead>
            <tr v-for="headerGroup in table.getHeaderGroups()" :key="headerGroup.id">
              <th
                class="sticky top-0 bg-gray-300 p-2"
                v-for="header in headerGroup.headers"
                :key="header.id"
                :colSpan="header.colSpan"
                :class="header.column.getCanSort() ? 'cursor-pointer select-none' : ''"
                @click="header.column.getToggleSortingHandler()?.($event)"
              >
                <div class="flex items-center gap-3">
                  <FlexRender
                    :render="header.column.columnDef.header"
                    :props="header.getContext()"
                  />

                  <div class="w-4">
                    {{ { asc: ' 🔼', desc: ' 🔽' }[header.column.getIsSorted() as string] }}
                  </div>

                  <template v-if="header.column.getCanFilter()">
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
              v-for="row in table.getRowModel().rows"
              :key="row.id"
            >
              <td class="p-2" v-for="cell in row.getVisibleCells()" :key="cell.id">
                <FlexRender :render="cell.column.columnDef.cell" :props="cell.getContext()" />
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
