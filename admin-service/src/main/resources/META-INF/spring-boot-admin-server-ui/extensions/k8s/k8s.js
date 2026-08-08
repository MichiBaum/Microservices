SBA.use({
  install({ viewRegistry }) {
    viewRegistry.addView({
      name: 'k8s',
      path: '/k8s',
      component: {
        template: `
          <div class="m-6">
            <h1 class="text-2xl font-bold mb-4">Kubernetes Cluster</h1>
            
            <div class="mb-4 flex items-center gap-2">
              <label for="k8s-namespace" class="font-medium">Namespace:</label>
              <input 
                id="k8s-namespace" 
                v-model="namespace" 
                @change="fetchData" 
                placeholder="Filter by namespace..." 
                class="px-3 py-1 border border-gray-300 rounded"
              />
              <button @click="fetchData" class="px-4 py-1 bg-blue-600 text-white rounded hover:bg-blue-700">
                Refresh
              </button>
            </div>

            <div v-if="loading" class="text-gray-600 my-4">Loading Kubernetes data...</div>
            <div v-if="error" class="text-red-600 my-4">{{ error }}</div>

            <div v-if="!loading && !error">
              <div class="mb-8">
                <h2 class="text-xl font-semibold mb-2">Pods ({{ pods.length }})</h2>
                <div class="overflow-x-auto">
                  <table class="min-w-full border-collapse border border-gray-300">
                    <thead>
                      <tr class="bg-gray-100 text-left">
                        <th class="p-2 border border-gray-300">Name</th>
                        <th class="p-2 border border-gray-300">Namespace</th>
                        <th class="p-2 border border-gray-300">Status</th>
                        <th class="p-2 border border-gray-300">Pod IP</th>
                        <th class="p-2 border border-gray-300">Node</th>
                        <th class="p-2 border border-gray-300">Containers</th>
                        <th class="p-2 border border-gray-300">Creation Time</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="pod in pods" :key="pod.name" class="hover:bg-gray-50">
                        <td class="p-2 border border-gray-300 font-mono text-sm">{{ pod.name }}</td>
                        <td class="p-2 border border-gray-300">{{ pod.namespace }}</td>
                        <td class="p-2 border border-gray-300">
                          <span :class="getStatusClass(pod.status)" class="px-2 py-0.5 rounded text-xs font-semibold">
                            {{ pod.status }}
                          </span>
                        </td>
                        <td class="p-2 border border-gray-300 font-mono text-sm">{{ pod.podIp || '-' }}</td>
                        <td class="p-2 border border-gray-300">{{ pod.nodeName || '-' }}</td>
                        <td class="p-2 border border-gray-300">{{ pod.containers && pod.containers.length ? pod.containers.join(', ') : '-' }}</td>
                        <td class="p-2 border border-gray-300 text-sm">{{ pod.creationTimestamp || '-' }}</td>
                      </tr>
                      <tr v-if="pods.length === 0">
                        <td colspan="7" class="p-4 text-center text-gray-500">No pods found</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>

              <div>
                <h2 class="text-xl font-semibold mb-2">Services ({{ services.length }})</h2>
                <div class="overflow-x-auto">
                  <table class="min-w-full border-collapse border border-gray-300">
                    <thead>
                      <tr class="bg-gray-100 text-left">
                        <th class="p-2 border border-gray-300">Name</th>
                        <th class="p-2 border border-gray-300">Namespace</th>
                        <th class="p-2 border border-gray-300">Type</th>
                        <th class="p-2 border border-gray-300">Cluster IP</th>
                        <th class="p-2 border border-gray-300">Ports</th>
                        <th class="p-2 border border-gray-300">Selector</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="svc in services" :key="svc.name" class="hover:bg-gray-50">
                        <td class="p-2 border border-gray-300 font-mono text-sm">{{ svc.name }}</td>
                        <td class="p-2 border border-gray-300">{{ svc.namespace }}</td>
                        <td class="p-2 border border-gray-300">{{ svc.type }}</td>
                        <td class="p-2 border border-gray-300 font-mono text-sm">{{ svc.clusterIp || '-' }}</td>
                        <td class="p-2 border border-gray-300 text-sm">
                          <div v-for="port in svc.ports" :key="port.port">
                            {{ port.port }}{{ port.targetPort ? ':' + port.targetPort : '' }}/{{ port.protocol }}
                          </div>
                        </td>
                        <td class="p-2 border border-gray-300 text-sm">
                          <div v-for="(val, key) in svc.selector" :key="key">
                            {{ key }}: {{ val }}
                          </div>
                        </td>
                      </tr>
                      <tr v-if="services.length === 0">
                        <td colspan="6" class="p-4 text-center text-gray-500">No services found</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        `,
        data() {
          return {
            pods: [],
            services: [],
            namespace: '',
            loading: false,
            error: null
          };
        },
        methods: {
          getStatusClass(status) {
            if (status === 'Running') return 'bg-green-100 text-green-800';
            if (status === 'Pending') return 'bg-yellow-100 text-yellow-800';
            if (status === 'Failed') return 'bg-red-100 text-red-800';
            return 'bg-gray-100 text-gray-800';
          },
          async fetchData() {
            this.loading = true;
            this.error = null;
            try {
              const nsParam = this.namespace ? '?namespace=' + encodeURIComponent(this.namespace) : '';
              const [podsRes, servicesRes] = await Promise.all([
                fetch('/api/k8s/pods' + nsParam).then(r => {
                  if (!r.ok) throw new Error('Failed to fetch pods: ' + r.status + ' ' + r.statusText);
                  return r.json();
                }),
                fetch('/api/k8s/services' + nsParam).then(r => {
                  if (!r.ok) throw new Error('Failed to fetch services: ' + r.status + ' ' + r.statusText);
                  return r.json();
                })
              ]);
              this.pods = podsRes;
              this.services = servicesRes;
            } catch (err) {
              this.error = err.message || 'Error loading Kubernetes cluster details';
            } finally {
              this.loading = false;
            }
          }
        },
        created() {
          this.fetchData();
        }
      },
      label: 'Kubernetes',
      order: 100
    });
  }
});
