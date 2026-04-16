package com.renacegest.servlet;

import com.renacegest.dao.InMemoryRenaceGestRepository;
import com.renacegest.dao.MySQLRenaceGestRepository;
import com.renacegest.dao.RenaceGestRepository;
import com.renacegest.db.DBConnection;

import jakarta.servlet.http.HttpServletRequest;

public final class SessionRepositoryResolver {
    private SessionRepositoryResolver() {
    }

    public static RenaceGestRepository resolve(HttpServletRequest request) {
        String profile = AuthUtil.getCurrentDbProfile(request);
        DBConnection.setCurrentProfile(profile);

        try {
            if ("REAL".equalsIgnoreCase(profile)) {
                return MySQLRenaceGestRepository.getRealInstance();
            }
            return MySQLRenaceGestRepository.getPruebaInstance();
        } catch (Throwable ex) {
            System.err.println("Fallo al resolver repositorio MySQL. Se usa InMemory temporalmente: " + ex.getMessage());
            return InMemoryRenaceGestRepository.getInstance();
        }
    }
}
