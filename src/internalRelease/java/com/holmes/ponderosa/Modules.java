package com.holmes.ponderosa;

final class Modules {
  static Object[] list(PonderosaApp app) {
    return new Object[] {
        new PonderosaModule(app),
        new InternalReleasePonderosaModule()
    };
  }

  private Modules() {
    // No instances.
  }
}
