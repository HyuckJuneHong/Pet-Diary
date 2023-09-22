package kr.co.petdiary.global.auth.context;

import kr.co.petdiary.owner.entity.Owner;

public class OwnerThreadLocal {
    private static final ThreadLocal<Owner> ownerThreadLocal;

    static {
        ownerThreadLocal = new ThreadLocal<>();
    }

    public static Owner getOwner() {
        return ownerThreadLocal.get();
    }

    public static void setOwner(Owner owner) {
        ownerThreadLocal.set(owner);
    }

    public static boolean isOwner() {
        return ownerThreadLocal.get() != null;
    }

    public static void clear() {
        ownerThreadLocal.remove();
    }
}
