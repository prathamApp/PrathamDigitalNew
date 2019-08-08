package com.pratham.prathamdigital.custom.view_animators;

public enum Techniques {
    Flash(FlashAnimator.class),
    ZoomIn(ZoomInAnimator.class),
    BounceInUp(BounceInUpAnimator.class),
    SlideOutDown(SlideOutDownAnimator.class),
    FlipIn(FlipInXAnimator.class);
    private Class animatorClazz;

    private Techniques(Class clazz) {
        animatorClazz = clazz;
    }

    public BaseViewAnimator getAnimator() {
        try {
            return (BaseViewAnimator) animatorClazz.newInstance();
        } catch (Exception e) {
            throw new Error("Can not init animatorClazz instance");
        }
    }
}
