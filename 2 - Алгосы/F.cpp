#include <bits/stdc++.h>
using namespace std;

vector<string> readArray() {
    vector<string> array;
    string x;
    while (cin >> x) {
        array.push_back(x);
    }
    return array;
}

struct N {
    bool operator()(const string& a, const string& b) const {
        return b + a > a + b;
    }
};

priority_queue<string, vector<string>, N> convertToPriorityQueue(const vector<string>& numbers) {
    priority_queue<string, vector<string>, N> array;
    for (const string& number : numbers) {
        array.push(number);
    }
    return array;
}

string buildMaxNumber(priority_queue<string, vector<string>, N>& array) {
    string result;
    while (!array.empty()) {
        result += array.top();
        array.pop();
    }
    return result;
}

int main() {
    vector<string> numbers = readArray();
    auto array = convertToPriorityQueue(numbers);
    string maxNumber = buildMaxNumber(array);
    cout << maxNumber << endl;
    return 0;
}
