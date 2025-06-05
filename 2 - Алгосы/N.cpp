#include <bits/stdc++.h>
using namespace std;

class DisjointSetUnion {
    int* parent_nodes;

public:
    DisjointSetUnion(int num_nodes) {
        parent_nodes = new int[num_nodes];
        for (int i = 0; i < num_nodes; i++)
            parent_nodes[i] = i;
    }

    ~DisjointSetUnion() {
        delete[] parent_nodes;
    }

    int find_root(int node) {
        if (parent_nodes[node] != node)
            parent_nodes[node] = find_root(parent_nodes[node]);
        return parent_nodes[node];
    }

    void merge_sets(int node_a, int node_b) {
        node_a = find_root(node_a);
        node_b = find_root(node_b);
        if (node_a != node_b)
            parent_nodes[node_b] = node_a;
    }
};

void readKey(int* key_array, int index) {
    cin >> key_array[index];
    key_array[index]--;
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);

    int num_keys;
    cin >> num_keys;

    int* keys = new int[num_keys];
    for (int i = 0; i < num_keys; i++)
        readKey(keys, i);

    DisjointSetUnion dsu(num_keys);
    for (int i = 0; i < num_keys; i++)
        dsu.merge_sets(i, keys[i]);

    unordered_set<int> unique_components;
    for (int i = 0; i < num_keys; i++)
        unique_components.insert(dsu.find_root(i));

    cout << unique_components.size() << '\n';

    delete[] keys;
    return 0;
}
