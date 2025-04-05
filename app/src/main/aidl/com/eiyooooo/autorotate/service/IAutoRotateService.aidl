package com.eiyooooo.autorotate.service;

import com.eiyooooo.autorotate.data.ScreenConfig;

interface IAutoRotateService {

    void updateConfigs(in List<ScreenConfig> configs) = 1;

    void destroy() = 16777114;
}
