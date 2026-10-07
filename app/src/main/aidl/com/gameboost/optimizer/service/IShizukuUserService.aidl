package com.gameboost.optimizer.service;

import android.os.Bundle;

interface IShizukuUserService {
    void destroy() = 16777114;
    void exit() = 1;
    Bundle executeCommand(String command) = 2;
}
