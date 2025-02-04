import React, { useState, useRef, useCallback, useMemo } from 'react';
import {
  View,
  TouchableWithoutFeedback,
  StyleSheet,
  useWindowDimensions,
  Image,
} from 'react-native';
import { flatten } from 'lodash';
import { Icon as NBIcon } from 'native-base';
import { Box, OverlayContainer, Pressable, Token, useUserDevice, Text } from '@onekeyhq/components';
import { ChevronDown } from '@onekeyhq/components/src/Icon/react/mini';
import MobileNetwork from './MobileNetwork';
import DesktopNetwork from './DesktopNetwork';

interface CloseButtonProps {
  onClose?: () => void;
  backgroundColor?: string;
}

export function CloseBackDrop({ onClose, backgroundColor }: CloseButtonProps) {
  const { width, height } = useWindowDimensions();
  return (
    <TouchableWithoutFeedback onPress={onClose}>
      <View
        style={{
          width,
          height,
          backgroundColor,
          ...StyleSheet.absoluteFillObject,
        }}
      />
    </TouchableWithoutFeedback>
  );
}

export type SelectItem<T = any> = {
  label: string;
  description?: string | React.ReactElement<any, any>;
  value: T;
  tokenProps?: React.ComponentProps<typeof Token>;
  iconUrl?: string;
  OutlineIcon?: string;
  MiniIcon?: string;
  destructive?: boolean;
  color?: string | undefined; // 修改这里的类型
  badge?: string;
  leading?: React.ReactNode;
  trailing?: React.ReactNode;
};

export type SelectGroupItem<T = string> = {
  title: string;
  options: SelectItem<T>[];
};

export type IDropdownPosition =
  | 'center'
  | 'left'
  | 'right'
  | 'top-left'
  | 'top-right'
  | 'top-center';
export type IDropdownProps = React.ComponentProps<typeof Box>;
export type SelectProps<T = string> = {
  title?: string;
  headerShown?: boolean;
  options: (SelectItem<T> | SelectGroupItem<T>)[];
  value?: T;
  defaultValue?: T;
  containerProps?: React.ComponentProps<typeof Box>;
  triggerProps?: React.ComponentProps<typeof Pressable>;
  dropdownProps?: IDropdownProps;
  renderTrigger?: (options: {
    activeOption: SelectItem<T>;
    isHovered: boolean;
    isFocused: boolean;
    isPressed: boolean;
    visible: boolean;
    onPress?: () => void;
  }) => React.ReactNode;
  renderItem?: (
    item: SelectItem<T>,
    isActive: boolean,
    onChange?: (v: T, item: SelectItem<T>) => void,
  ) => React.ReactNode;
  dropdownPosition?: IDropdownPosition;
  onChange?: (v: T, item: SelectItem<T>) => void;
  footer?: React.ReactNode;
  footerText?: string;
  onPressFooter?: () => void;
  onModalHide?: () => void;
  isTriggerPlain?: boolean;
  triggerSize?: 'xl' | 'default' | string | undefined;
  activatable?: boolean;
  visible?: boolean | undefined;
  onVisibleChange?: (visible: boolean) => void;
  triggerEle?: HTMLElement | View | null;
  setPositionOnlyMounted?: boolean;
  positionTranslateY?: number;
  withReactModal?: boolean;
  autoAdjustPosition?: boolean;
  outerContainerRef?: React.MutableRefObject<unknown>;
  noTrigger?: boolean;
};

export type ChildProps<T> = Pick<
  SelectProps<T>,
  | 'dropdownProps'
  | 'options'
  | 'onChange'
  | 'title'
  | 'footer'
  | 'footerText'
  | 'onPressFooter'
  | 'renderItem'
  | 'headerShown'
  | 'onModalHide'
  | 'isTriggerPlain'
  | 'triggerSize'
  | 'activatable'
  | 'dropdownPosition'
  | 'triggerEle'
  | 'setPositionOnlyMounted'
  | 'positionTranslateY'
  | 'withReactModal'
  | 'autoAdjustPosition'
  | 'outerContainerRef'
> & {
  toggleVisible: () => void;
  visible: boolean;
  activeOption: SelectItem<T>;
  triggerRef: React.RefObject<unknown>;
};

const defaultProps = {
  headerShown: true,
  dropdownPosition: 'center',
  isTriggerPlain: false,
  triggerSize: 'default',
  activatable: true,
  visible: undefined,
  onVisibleChange: null,
} as const;

function SelectNetWork<T = any>({
  options,
  value,
  containerProps,
  triggerProps,
  dropdownProps,
  renderTrigger,
  renderItem,
  onChange,
  defaultValue,
  title,
  footer,
  footerText,
  onPressFooter,
  headerShown,
  isTriggerPlain,
  triggerSize,
  activatable,
  dropdownPosition,
  visible: selectVisible,
  onVisibleChange,
  setPositionOnlyMounted,
  positionTranslateY,
  onModalHide,
  withReactModal,
  autoAdjustPosition,
  outerContainerRef,
  noTrigger,
}: SelectProps<T>) {
  const triggerRef = useRef<HTMLElement | View>(null);
  const [innerVisible, setInnerVisible] = useState(false);
  const visible = selectVisible ?? innerVisible;
  const { size } = useUserDevice();
  const toggleVisible = useCallback(() => {
    const newVisible = !visible;
    setInnerVisible(newVisible);
    onVisibleChange?.(newVisible);
  }, [onVisibleChange, visible]);

  const [innerValue, setInnerValue] = useState<T | undefined>(defaultValue);
  const currentActiveValue = value ?? innerValue;

  const activeOption = useMemo(() => {
    if (
      (options as SelectGroupItem<T>[]).every((option) =>
        Array.isArray(option.options),
      )
    ) {
      const groupOptions = options as SelectGroupItem<T>[];
      const flattenedOptions = flatten(
        groupOptions.map((option) => option.options),
      );
      return (flattenedOptions.find(
        (option) => option.value === currentActiveValue,
      ) ?? {}) as SelectItem<T>;
    }

    return ((options as SelectItem<T>[]).find(
      (option) => option.value === currentActiveValue,
    ) ?? {}) as SelectItem<T>;
  }, [currentActiveValue, options]);

  const handleChange = useCallback(
    (v: T, option: SelectItem<T>) => {
      setTimeout(() => {
        if (typeof v === 'function') {
          v();
        } else {
          if (value === undefined) {
            setInnerValue(v);
          }
          onChange?.(v, option);
        }
      }, 500);
      toggleVisible();
    },
    [onChange, toggleVisible, value],
  );

  const handlePressFooter = useCallback(() => {
    toggleVisible();
    onPressFooter?.();
  }, [onPressFooter, toggleVisible]);

  const container = useMemo(() => {
    const childContainerProps = {
      visible,
      options,
      toggleVisible,
      dropdownProps,
      title,
      footer,
      footerText,
      activeOption,
      renderItem: renderItem as (
        item: import("@onekeyhq/components/src/Select/index").SelectItem<T>,
        isActive: boolean,
        onChange?: (v: T, item: import("@onekeyhq/components/src/Select/index").SelectItem<T>) => void
      ) => React.ReactNode,
      headerShown,
      onChange: handleChange,
      activatable,
      dropdownPosition,
      onPressFooter: handlePressFooter,
      triggerEle: triggerRef?.current,
      triggerRef,
      setPositionOnlyMounted,
      positionTranslateY,
      withReactModal,
      outerContainerRef,
      onModalHide: () => {
        if (visible) {
          toggleVisible();
        }
        onModalHide?.();
      },
    };
    if (['SMALL', 'NORMAL'].includes(size)) {
      return <MobileNetwork<T> {...childContainerProps} />;
    }
    if (!childContainerProps.visible) {
      return null;
    }
    return (
      <OverlayContainer>
        <CloseBackDrop onClose={toggleVisible} />
        <DesktopNetwork<T>
          {...childContainerProps}
          autoAdjustPosition={autoAdjustPosition}
        />
      </OverlayContainer>
    );
  }, [
    visible,
    options,
    toggleVisible,
    dropdownProps,
    title,
    footer,
    footerText,
    activeOption,
    renderItem,
    headerShown,
    handleChange,
    activatable,
    dropdownPosition,
    handlePressFooter,
    setPositionOnlyMounted,
    positionTranslateY,
    withReactModal,
    outerContainerRef,
    size,
    autoAdjustPosition,
    onModalHide,
  ]);

  return noTrigger ? (
    container
  ) : (
    <Box ref={triggerRef} position="relative" {...containerProps}>
      <Pressable onPress={toggleVisible} {...triggerProps}>
        {({ isHovered, isFocused, isPressed }) =>
          renderTrigger?.({
            activeOption,
            isHovered,
            isFocused,
            isPressed,
            visible,
            onPress: toggleVisible,
          }) ?? (
            <Box
              display="flex"
              flexDirection="row"
              alignItems="center"
              py={triggerSize === 'xl' ? 3 : 2}
              pl="3"
              pr="2.5"
              borderWidth={isTriggerPlain ? undefined : '1'}
              borderColor={
                // eslint-disable-next-line no-nested-ternary
                isTriggerPlain
                  ? undefined
                  : // eslint-disable-next-line no-nested-ternary
                  visible
                  ? 'focused-default'
                  : isHovered
                  ? 'border-hovered'
                  : 'border-default'
              }
              borderRadius="xl"
              bg={
                // eslint-disable-next-line no-nested-ternary
                visible
                  ? 'action-secondary-pressed'
                  : // eslint-disable-next-line no-nested-ternary
                  isHovered
                  ? 'action-secondary-hovered'
                  : isTriggerPlain
                  ? undefined
                  : 'action-secondary-default'
              }
            >
              <Box
                display="flex"
                flex={1}
                flexDirection="row"
                alignItems="center"
                mr="1"
              >
                {!!activeOption.tokenProps && (
                  <Box mr="3">
                    <Token
                      size={activeOption.description ? 8 : 6}
                      {...activeOption.tokenProps}
                    />
                  </Box>
                )}
                {!!activeOption.iconUrl && (
                  <Box mr="3">
                    <Image
                      source={{ uri: activeOption.iconUrl }}
                      style={{ width: 24, height: 24 }}
                    />
                  </Box>
                )}
                <Box flex={1}>
                  <Text
                    typography={
                      triggerSize === 'xl'
                        ? 'Body1'
                        : // triggerSize === 'default'
                          { sm: 'Body1', md: 'Body2' }
                    }
                    numberOfLines={1}
                    flex={1}
                    isTruncated
                  >
                    {activeOption.label ?? '-'}
                  </Text>
                  {activeOption.description && (
                    <Text typography="Body2" color="text-subdued">
                      {activeOption.description ?? '-'}
                    </Text>
                  )}
                </Box>
              </Box>
              <NBIcon as={ChevronDown} size={5} ml="auto" />
            </Box>
          )
        }
      </Pressable>
      {container}
    </Box>
  );
}

SelectNetWork.defaultProps = defaultProps;

export default SelectNetWork;