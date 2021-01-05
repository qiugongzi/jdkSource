
                referent = null;
            }
        });

        Entry fhe[] = countMap.values().toArray(new Entry[countMap.size()]);
        Arrays.sort(fhe,
                Comparator.comparingInt(Entry::getInstanceCount).reversed());
        return fhe;
    }
}
