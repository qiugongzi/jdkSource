
    }

    final void setCaches(RepositoryIdCache cache) {
        this.cache = cache;
    }

}

public class RepositoryIdCache extends Hashtable {

    private RepositoryIdPool pool = new RepositoryIdPool();

    public RepositoryIdCache() {
        pool.setCaches(this);
    }

    public final synchronized RepositoryId getId(String key) {
        RepositoryId repId = (RepositoryId)super.get(key);

        if (repId != null)
            return repId;
        else {

            repId = new RepositoryId(key);
            put(key, repId);
            return repId;
        }

    }
}
