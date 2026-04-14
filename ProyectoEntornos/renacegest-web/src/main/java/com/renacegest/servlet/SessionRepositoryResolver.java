package com.renacegest.servlet;

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
        if ("REAL".equalsIgnoreCase(profile)) {
            return MySQLRenaceGestRepository.getRealInstance();
        }
        return MySQLRenaceGestRepository.getPruebaInstance();
    }
}
