package com.holmes.ponderosa;

final class Modules {
  static Object[] list(PonderosaApp app) {
    return new Object[] {
        new PonderosaModule(app)
    };
  }

  private Modules() {
    // No instances.
  }
}
