package com.example.logbook4;

import androidx.annotation.NonNull;

public interface RequestPermission {
    void onRequestPermissionResult(int requestCode, @NonNull String[] permission, @NonNull int[] grandResults);
}
