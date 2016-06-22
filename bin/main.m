//
//  main.m
//  movemouse
//
//  Created by KawakawaPlanning on 6/20/16.
//  Copyright © 2016 KawakawaPlanning. All rights reserved.
//

#import <Foundation/Foundation.h>
#include <AppKit/AppKit.h>
#define CLAMP(a, l, h) MIN(h, MAX(a, l))

int move(CGFloat width,CGFloat height,CGFloat dx,CGFloat dy) {

    NSPoint mouseLoc = [NSEvent mouseLocation];
    mouseLoc.x = CLAMP(mouseLoc.x + dx, 0, width - 1);
    mouseLoc.y = CLAMP(mouseLoc.y - dy, 0, height - 1);

    CGEventRef move = CGEventCreateMouseEvent(NULL, kCGEventMouseMoved,CGPointMake(mouseLoc.x, height - mouseLoc.y),0);
    CGEventSetIntegerValueField(move, kCGMouseEventDeltaX, (int)dx);
    CGEventSetIntegerValueField(move, kCGMouseEventDeltaY, (int)dy);
    CGEventPost(kCGHIDEventTap, move);

    if (CGEventSourceButtonState(kCGEventSourceStateHIDSystemState, kCGMouseButtonLeft)) {
        CGEventSetType(move, kCGEventLeftMouseDragged);
        CGEventSetIntegerValueField(move, kCGMouseEventButtonNumber, kCGMouseButtonLeft);
        CGEventPost(kCGHIDEventTap, move);
    }
    if (CGEventSourceButtonState(kCGEventSourceStateHIDSystemState, kCGMouseButtonRight)) {
        CGEventSetType(move, kCGEventRightMouseDragged);
        CGEventSetIntegerValueField(move, kCGMouseEventButtonNumber, kCGMouseButtonRight);
        CGEventPost(kCGHIDEventTap, move);
    }
    if (CGEventSourceButtonState(kCGEventSourceStateHIDSystemState, kCGMouseButtonCenter)) {
        CGEventSetType(move, kCGEventOtherMouseDragged);
        CGEventSetIntegerValueField(move, kCGMouseEventButtonNumber, kCGMouseButtonCenter);
        CGEventPost(kCGHIDEventTap, move);
    }
    CFRelease(move);

    return 0;
}

int press(CGFloat height,bool left) {

    NSPoint mouseLoc = [NSEvent mouseLocation];

    int event;
    if(left){
      event = kCGEventLeftMouseDown;
    }else{
      event = kCGEventRightMouseDown;
    }

    CGEventRef click = CGEventCreateMouseEvent(NULL, event,CGPointMake(mouseLoc.x,height-mouseLoc.y), kCGMouseButtonLeft);
    CGEventPost(kCGHIDEventTap, click);
    CFRelease(click);

    return 0;
}

int release(CGFloat height,bool left) {

    NSPoint mouseLoc = [NSEvent mouseLocation];

    int event;
    if(left){
      event = kCGEventLeftMouseUp;
    }else{
      event = kCGEventRightMouseUp;
    }

    CGEventRef click = CGEventCreateMouseEvent(NULL, event,CGPointMake(mouseLoc.x,height-mouseLoc.y), kCGMouseButtonLeft);
    CGEventPost(kCGHIDEventTap, click);
    CFRelease(click);

    return 0;
}
